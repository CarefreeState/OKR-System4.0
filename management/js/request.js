function toLogin() {
  jQuery.toast({
    heading: "提示",
    text: "前往登录",
    icon: "info",
    allowToastClose: true,
  });
  setTimeout(function () {
    location.href = "../login.html";
  }, 3000);
}

function toUserManagement() {
  jQuery.toast({
    heading: "提示",
    text: "前往系统用户管理",
    icon: "info",
    allowToastClose: true,
  });
  setTimeout(function () {
    location.href = "../user-management.html";
  }, 3000);
}

// 根据 key 获取 url 中对应的 value
function getParamValue(key) {
  // 1.得到当前url的参数部分
  var params = location.search;
  // 2.去除“?”
  if (params.indexOf("?") >= 0) {
    params = params.substring(1);
    // 3.根据“&”将参数分割成多个数组
    var paramArray = params.split("&");
    // 4.循环对比 key，并返回查询的 value
    if (paramArray.length >= 1) {
      for (var i = 0; i < paramArray.length; i++) {
        // key=value
        var item = paramArray[i].split("=");
        if (item[0] == key) {
          return item[1];
        }
      }
    }
  }
  return null;
}

function setToken(token) {
  if (token) {
    console.log("设置 token：" + token);
    jQuery.cookie("token", token, {
      expires: 365,
    });
  }
}

function getToken() {
  var token = jQuery.cookie("token");
  if (token) {
    console.log("获取 token：" + token);
    return jQuery.cookie("token");
  } else {
    toLogin();
  }
}

function toastHeader(heading, icon, header) {
  if (header) {
    jQuery.toast({
      heading: heading,
      icon: icon,
      text: decodeURIComponent(header),
      allowToastClose: true,
    });
  }
}

function getBaseUrl(u) {
  return "https://api.bitterfree.cn" + u;
}

function jsonRequestWithToken(u, m, d, s) {
  jQuery.ajax({
    crossDomain: true,
    url: getBaseUrl(u),
    method: m,
    contentType: "application/json; charset=utf8",
    headers: {
      token: getToken(),
    },
    data: JSON.stringify(d),
    success: function (body) {
      if (body.code === 200) {
        s(body.data);
      } else {
        jQuery.toast({
          heading: "异常",
          text: body.message,
          icon: "warning",
          allowToastClose: true,
        });
        if (body.code === 2001) {
          toLogin();
        }
      }
    },
    error: function () {
      //提示信息
      $.toast({
        heading: "错误",
        text: "访问出现问题",
        icon: "error",
        allowToastClose: true,
      });
    },
    complete: function (xhr) {
      setToken(xhr.getResponseHeader("Token"));
      toastHeader("提示", "info", xhr.getResponseHeader("info"));
      toastHeader("警告", "warning", xhr.getResponseHeader("warn"));
      toastHeader("错误", "error", xhr.getResponseHeader("error"));
    },
  });
}

function login(u, p) {
  jQuery.ajax({
    crossDomain: true,
    url: getBaseUrl("/user/login"),
    method: "POST",
    contentType: "application/json; charset=utf8",
    headers: {
      "Login-Type": "jOKQE5",
    },
    data: JSON.stringify({
      passwordLoginDTO: {
        username: u,
        password: p,
      },
    }),
    // 3. 处理响应
    success: function (body) {
      if (body.code === 200) {
        jQuery.toast({
          heading: "成功",
          text: "登录成功",
          icon: "success",
          allowToastClose: true,
        });
        setToken(body.data.Token);
        toUserManagement();
      } else {
        jQuery.toast({
          heading: "异常",
          text: body.message,
          icon: "warning",
          allowToastClose: true,
        });
      }
    },
    error: function () {
      //提示信息
      $.toast({
        heading: "错误",
        text: "访问出现问题",
        icon: "error",
        allowToastClose: true,
      });
    },
  });
}
