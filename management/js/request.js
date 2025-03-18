function toLogin() {
  jQuery.toast({
    heading: "提示",
    text: "前往登录",
    icon: "info",
    allowToastClose: true,
  });
  setTimeout(function () {
    location.href = "login.html";
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
    location.href = "user-management.html";
  }, 3000);
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

function formRequestWithToken(u, m, d, s) {
  fetch(getBaseUrl(u), {
    method: m,
    headers: {
      token: getToken(),
    },
    body: d ? d : null,
  })
    .then((response) => {
      var headers = response.headers;
      setToken(headers.get("Token"));
      toastHeader("提示", "info", headers.get("info"));
      toastHeader("警告", "warning", headers.get("warn"));
      toastHeader("错误", "error", headers.get("error"));
      return response.json();
    })
    .then((data) => {
      if (data.code === 200) {
        s(data.data);
      } else {
        jQuery.toast({
          heading: "异常",
          text: data.message,
          icon: "warning",
          allowToastClose: true,
        });
        if (data.code === 2001) {
          toLogin();
        }
      }
    })
    .finally(() => {})
    .catch((error) => {
      //提示信息
      console.error("访问出现问题:", error);
      $.toast({
        heading: "错误",
        text: "访问出现问题",
        icon: "error",
        allowToastClose: true,
      });
    });
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
    data: d ? JSON.stringify(d) : null,
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
