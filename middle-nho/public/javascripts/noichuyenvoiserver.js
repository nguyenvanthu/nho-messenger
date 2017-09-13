var CONFIG;
if (location.protocol != 'https:') {
  CONFIG = {url: 'ws://139.162.5.38:9998/websocket'};
} else {
  CONFIG = {url: 'wss://nhomessenger.com/websocket'};
}
var ACCESS_TOKEN = '1145713548841076|ExP57T-4qTauVNG0tlZ9cNNwwVk';
var DEBUG_MODE = false;
var SETUP_TIMEOUT_MS = 7000;

var MessageType = {
  CHAT: 8,
  LEAVE_CHANNEL_RESPONE: 14,
  POKE_REQUEST: 43,
  NOISE_LEVEL: 52,
  STICKER_CHAT: 55,
  PICK_DATA_MESSAGE: 57,
  MAKE_OBJECT_CHAT_RESPONSE: 59,
  DELETE_LIVE_OBJECT: 61,
  JOINED_TO_CHANNEL_RESPONSE: 82,
  BUBBLE_CHAT: 86,
  FAKE_LOGIN: 87,
  FAKE_LOGIN_RESPONSE: 88
};

var StatusUserInChannel = {
  INVITED_USER_LOG_OUT: 0,
  INVITED_USER_OFFLINE: 1,
  INVITED_USER_BUSY: 2,
  INVITED_USER_ON_PAUSE: 3,
  INVITED_USER_AVAILABLE: 4,
  USER_CHAT_WITH_BOT: 5,
  USER_JOIN_LOG_OUT: 6,
  USER_JOIN_OFFLINE: 7
};

/**
 * Service to manage all API interactions.
 * @constructor
 */
MessageService = function() {
  /** Native websocket */
  this.socket;

  /** 
   * Channel ID. Empty string if have not been set up.
   */
  this.channelId = '';

  /** 
   * User A's, the inviter, ID.
   */
  this.userAId = this.getUrlParameter('moi');

  /** 
   * User A's, the inviter, name.
   */
  this.userAName = 'Người kia';

  /** 
   * User A's, the inviter, avatar.
   */
  this.userAAvatar = '';

  /** 
   * User A's, the inviter, avatar.
   */
  this.userAAvatar = '';

  /** 
   * User B's, this client, ID.
   */
  this.userBId;
  if (document.location.hash && document.location.hash.length > 2) {
    this.userBId = document.location.hash.substr(1);
  } else {
    this.userBId = this.createUUID();
    document.location.hash = this.userBId;
  }

  /** 
   * Whether user A is online.
   */
  this.isUserAOnline = false;

  /**
   * DrawSequence service.
   */
  this.drawSequence;

  var requestIds = this.getUrlParameter('request_ids');

  if (!this.userAId && !requestIds) {
    showMessage('App Nhớ không mở được, hình như là vì link hỏng bạn ạ.');
    document.location.hash = "";
    return;
  }

  if (!this.userAId && requestIds) {
    this.getUserIdFromFb(requestIds);
    return;
  }

  this.setupSocketConnection_();
};

/**
 * Gets User ID from Facebook.
 */
MessageService.prototype.getUserIdFromFb = function(requestIds) {
  window.fbAsyncInit = function() {
    FB.init({
      appId      : '1145713548841076',
      xfbml      : true,
      version    : 'v2.8'
    });

    var requestId = requestIds.split(',')[0];
    FB.api(
      '/' + requestId + '?access_token=' + ACCESS_TOKEN,
      'GET',
      {},
      function(response) {
        this.userAId = response.from.id;
        this.setupSocketConnection_();
      }.bind(this)
    );
  }.bind(this);

  (function(d, s, id){
     var js, fjs = d.getElementsByTagName(s)[0];
     if (d.getElementById(id)) {return;}
     js = d.createElement(s); js.id = id;
     js.src = "https://connect.facebook.net/en_US/sdk.js";
     fjs.parentNode.insertBefore(js, fjs);
   }(document, 'script', 'facebook-jssdk'));

}

/**
 * Sends a message.
 * @param {Object} message
 * sample 
 * "[8,0,{data: 
 *    [{"action":2,"index":13,"x":0.39123797,"y":0.47465932},{"action":2,"index":14,"x":0.3806302,"y":0.47607195},{"action":2,"index":15,"x":0.37299618,"y":0.4774268},{"action":1,"index":16,"x":0.37299618,"y":0.4774268}]
 *    , from: 121025085024145, to: b73e807f-485f-4a3f-ac63-4397f5cf7d33, messageId: 4, stickerType: 0)}]"
 */
MessageService.prototype.sendMessage = function(message) {
  this.socket.send(JSON.stringify(message));
  if (DEBUG_MODE) {
    console.log('SEND: ');
    console.log(message);
  }
}

/**
 * Sends an í ới notification.
 */
MessageService.prototype.sendNotification = function() {
  this.sendMessage([MessageType.POKE_REQUEST, 0, this.userBId, this.channelId]);
  trackEvent(CATEGORY_MISC, ACTION_POKE, true /* opt_isMe */);
}

/**
 * Sends a delete all live objects message.
 */
MessageService.prototype.sendDeleteAllLiveObjsMessage = function(liveObjIdsToDelete) {
  this.sendMessage([MessageType.DELETE_LIVE_OBJECT, 0, this.channelId, this.userBId, liveObjIdsToDelete, null]);
  trackEvent(CATEGORY_VEGIDUOCNAY, ACTION_CLEAR_SCREEN, true /* opt_isMe */);
}

/**
 * Sends pick data message.
 */
MessageService.prototype.sendPickDataMessage = function(liveObjId, newPos) {
  // Fix to 5 decimal points because PuArray will break for long float.
  var data = {value: [newPos.x.toFixed(5), newPos.y.toFixed(5)]};
  this.sendMessage([MessageType.PICK_DATA_MESSAGE, 0, this.channelId, liveObjId,
      this.userBId, data, 0, null]);
  trackEvent(CATEGORY_VEGIDUOCNAY, ACTION_MOVE_OBJECT, true /* opt_isMe */);
}

/**
 * Sends bubble message.
 */
MessageService.prototype.sendBubbleMessage = function(message, messageId) {
  this.sendMessage([MessageType.BUBBLE_CHAT, messageId, this.userBId, this.channelId, (new Date()).getTime(), {value: message}, null]);
  trackEvent(CATEGORY_MISC, ACTION_BUBBLE_CHAT, true /* opt_isMe */);
}
/**
 * Generates an UUID for live object.
 * @return {string} Live object's ID.
 */
MessageService.prototype.createLiveObject = function() {
  return this.createUUID();
}

/**
 * Subcribes to normal draw messages.
 * @param {Function} onNormalDraw
 */
MessageService.prototype.subscribeNormalDraw = function(onNormalDraw) {
  this.drawSequence = new DrawSequence(onNormalDraw);
};

/**
 * Creates UUID.
 * @return {string} UUID.
 */
MessageService.prototype.createUUID = function() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
}

/**
 * Extracts url parameter.
 * @param {string} param
 * @return {string} Param's value.
 */
MessageService.prototype.getUrlParameter = function(param) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === param) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

/**
 * Sets up a socket connection.
 * @private
 */
MessageService.prototype.setupSocketConnection_ = function() {
  try {
    this.socket = new WebSocket(CONFIG.url);
    showMessage('Đợt một lát...');
  } catch (e) {
    this.handleSocketFailError_();
  }
  this.socket.onopen = this.onSocketOpen_.bind(this);

  this.socket.onmessage = this.onSocketMessage_.bind(this);

  this.socket.onclose = function(evt) {
    // TODO(vietanh): close properly;
  }.bind(this);

  this.socket.onerror = this.onSocketError_.bind(this);
};

/**
 * Handles the socket error event.
 * @param {MessageEvent} resp Server response.
 * @param {string} opt_debug Optional debug string.
 * @private
 */
MessageService.prototype.onSocketError_ = function(resp, opt_debug) {
  if (DEBUG_MODE) {
    console.log((opt_debug ? opt_debug + ': ' : '') + resp);
  }
  trackError(JSON.stringify(resp));
};

/**
 * Handles the socket open event.
 * @param {MessageEvent} resp Server response.
 * @private
 */
MessageService.prototype.onSocketOpen_ = function(resp) {
  if (DEBUG_MODE) {
    console.log('connected');
  }
  trackChatInitiation();
  if (!this.channelId) {
    this.sendMessage([MessageType.FAKE_LOGIN, 0 /* messageId */, this.userBId, this.userAId]);
    window.setTimeout(function() {
      if (!this.channelId) {
        showMessage('Không kết nối được, ID người mời không đúng.');
        trackChatStatus(ACTION_WRONG_LINK);
      }
    }.bind(this), SETUP_TIMEOUT_MS);
  }
};

/**
 * Handles the socket receiving message event.
 * @param {MessageEvent} resp Server response.
 * @private
 */
MessageService.prototype.onSocketMessage_ = function(evt) {
  var resp = JSON.parse(evt.data);
  if (DEBUG_MODE && resp[0] != 52) {
    console.log(resp);
  }
  var messageType = resp[0];
  switch (messageType) {
    case MessageType.FAKE_LOGIN_RESPONSE:
      if (resp[2]) {
        this.channelId = resp[3];
        this.showStatusMessage_(resp[4]);
        this.userAName = resp[5] ? resp[5] : this.userAName;
        this.userAAvatar = resp[6] ? resp[6] : this.userAAvatar;
        showUserInfo(this.userAName, this.userAAvatar);
        trackChatStatus(ACTION_IS_READY);
        this.showUpsellIOs_();
      }
      break;
    case MessageType.JOINED_TO_CHANNEL_RESPONSE:
      this.isUserAOnline = true;
      hideMessage();
      trackChatStatus(ACTION_OTHER_GOES_ONLINE);
      break;
    case MessageType.CHAT:
      this.handleChatMessage_(resp);
      break;
    case MessageType.NOISE_LEVEL:
      this.handleSoundWave_(resp);
      trackSoundWave();
      break;
    case MessageType.MAKE_OBJECT_CHAT_RESPONSE:
      this.handleMakeLiveObjectMessage_(resp);
      trackEvent(CATEGORY_VEGIDUOCNAY, ACTION_CREATE_OBJECT);
      break;
    case MessageType.LEAVE_CHANNEL_RESPONE:
      this.isUserAOnline = false;
      showMessage(this.userAName + ' đang bận tí.', true /* opt_showPoke */);
      trackChatStatus(ACTION_OTHER_GOES_OFFLINE);
      break;
    case MessageType.DELETE_LIVE_OBJECT:
      this.handleDeleteLiveObjects_(resp);
      trackEvent(CATEGORY_VEGIDUOCNAY, ACTION_DELETE_OBJECT);
      break;
    case MessageType.PICK_DATA_MESSAGE:
      this.handleMoveLiveObject_(resp);
      trackEvent(CATEGORY_VEGIDUOCNAY, ACTION_MOVE_OBJECT);
      break;
    case MessageType.BUBBLE_CHAT:
      this.handleBubbleChat_(resp);
      trackEvent(CATEGORY_MISC, ACTION_BUBBLE_CHAT);
      break;
    case MessageType.STICKER_CHAT:
      this.handleSticker_(resp);
      trackEvent(CATEGORY_MISC, ACTION_TRIGGER_STICKER);
      break;
    default:
      this.onSocketError_(resp, 'Unknown resp');
      trackError(JSON.stringify(resp));
  }
}

/**
 * Handles a chat message.
 * @private
 */
MessageService.prototype.handleChatMessage_ = function(resp) {
  var messageId = resp[1];
  var senderId = resp[2];
  if (this.userAId !== senderId) {
    this.onSocketError_(resp, 'Mismatch sender');
    return;
  }
  var channelId = resp[3];
  var timestamp = resp[4];
  var data = JSON.parse(Base64.decode(resp[5].value));
  if (!(data instanceof Array)) {
    this.onSocketError_(resp, 'Invalid data');
    return;
  }

  if (this.drawSequence) {
    this.drawSequence.execute(data);
  }
  if (isShowingMessage) {
    hideMessage();
  }
  trackDraw(data.length);
};

/**
 * Handles a make live object message.
 * @private
 */
MessageService.prototype.handleMakeLiveObjectMessage_ = function(resp) {
  var fromId = resp[2];
  var toId = resp[3];
  var channelId = resp[4];
  var data = JSON.parse(resp[7]);
  var liveObjId = data.liveObjId;
  var strokes = data.strokes;

  this.drawSequence.execute(strokes);
};

/**
 * Handles a delete live objects message.
 * @private
 */
MessageService.prototype.handleDeleteLiveObjects_ = function(resp) {
  var liveObjIdsToDelete = resp[4];
  deleteAllLiveObjs(liveObjIdsToDelete);
};

/**
 * Handles a move live object message.
 * @private
 */
MessageService.prototype.handleMoveLiveObject_ = function(resp) {
  var channelId = resp[2];
  var liveObjId = resp[3];
  var fromId = resp[4];
  var data = resp[5];

  diChuyenTatCa(liveObjId, data.value[0], data.value[1]);
};

/**
 * Handles a bubble chat message.
 * @private
 */
MessageService.prototype.handleBubbleChat_ = function(resp) {
  var messageId = resp[1];
  var fromId = resp[2];
  var channelId = resp[3];
  var timestamp = resp[4];
  var value = Base64.decode(resp[5].value);

  chatBubble(value, false /* isMe */)
};

/**
 * Handles a sticker message.
 * @private
 */
MessageService.prototype.handleSticker_ = function(resp) {
  var channelId = resp[2];
  var timestamp = resp[3];
  var pos = resp[4].value;
  var sticker = resp[5];

  veSticker(sticker, pos);
};

/**
 * Handles a sound wave message.
 * @private
 */
MessageService.prototype.handleSoundWave_ = function(resp) {
  var soundLevel = resp[2];
  veSongAmThanh(soundLevel);
};

/**
 * Checks if this is a live object drawing message.
 * @private
 */
MessageService.prototype.handleSocketFailError_ = function() {
  // TODO: show UI error.
  console.log('can`t set up socket');
  showMessage('Xin lỗi, hiện giờ Nhớ chưa hỗ trợ trình duyệt này.')
};

/**
 * Shows upsell ios message.
 * @private
 */
MessageService.prototype.showUpsellIOs_ = function() {
  window.setTimeout(function() {
    $('.upsell').addClass('upsell--is-shown');
  }, 15000);
  $('.upsell .ios-wrapper').show();
  $('.upsell .inline-link').val(window.location.href);
  $('.upsell .inline-link').on('click', function () {
    $(this).select();
  });
};

/**
 * Shows the correct status message for the other user.
 * @param {number} userAStatus
 * @private
 */
MessageService.prototype.showStatusMessage_ = function(userAStatus) {
  switch (userAStatus) {
    case StatusUserInChannel.INVITED_USER_LOG_OUT:
      showMessage(this.userAName + ' không thể chat vào lúc này. Người kia đã thoát khỏi ứng dụng');
      trackChatStatus(ACTION_OTHER_STARTED_WITH_UNAVAILABLE);
      break;
    case StatusUserInChannel.INVITED_USER_OFFLINE:
    case StatusUserInChannel.INVITED_USER_BUSY:
    case StatusUserInChannel.INVITED_USER_ON_PAUSE:
    case StatusUserInChannel.USER_CHAT_WITH_BOT:
      showMessage(this.userAName + ' đang bận tí.', true /* opt_showPoke */);
      trackChatStatus(ACTION_OTHER_STARTED_WITH_OFFLINE);
      break;
    case StatusUserInChannel.INVITED_USER_AVAILABLE:
      hideMessage();
      trackChatStatus(ACTION_OTHER_STARTED_WITH_ONLINE);
      break;
    default:
      showMessage(this.userAName + ' không thể chat vào lúc này.');
      trackChatStatus(ACTION_OTHER_STARTED_WITH_UNAVAILABLE);
  }
};
