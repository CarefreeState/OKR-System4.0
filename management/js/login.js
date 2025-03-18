document.addEventListener("DOMContentLoaded", function () {
  const loginForm = document.getElementById("login-form");

  loginForm.addEventListener("submit", function (event) {
    event.preventDefault(); // 阻止表单默认提交行为

    // 获取输入值
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    // 简单验证
    if (username && password) {
      login(username, password);
    } else {
      jQuery.toast({
        heading: "异常",
        text: "请输入用户名和密码！",
        icon: "warning",
        allowToastClose: true,
      });
    }
  });
});
