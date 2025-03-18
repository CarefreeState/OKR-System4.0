jQuery("#input-username").blur(queryUsers);
jQuery("#input-nickname").blur(queryUsers);
jQuery("#select-userType").change(queryUsers);
jQuery("#input-current").blur(queryUsers);
jQuery("#input-pageSize").blur(queryUsers);

var avatarModal = document.getElementById("avatar-modal");
var modalImg = document.getElementById("modal-avatar");
var closeBtn = document.querySelector(".close");

var resetModal = document.getElementById("reset-modal");
var confirmReset = document.getElementById("confirm-reset");
var cancelReset = document.getElementById("cancel-reset");

var updateModal = document.getElementById("update-modal");
var confirmUpdate = document.getElementById("confirm-update");
var cancelUpdate = document.getElementById("cancel-update");
var newUserType = document.getElementById("new-user-type");

var toggleButton = document.getElementById("toggle-search");
var searchBar = document.querySelector(".search-bar");

toggleButton.addEventListener("click", function () {
  searchBar.classList.toggle("collapsed");
  // 更新按钮文字
  if (searchBar.classList.contains("collapsed")) {
    jQuery(".arrow-icon").text("▲");
  } else {
    jQuery(".arrow-icon").text("▼");
  }
});

closeBtn.addEventListener("click", function () {
  avatarModal.style.display = "none";
});

confirmReset.addEventListener("click", function () {
  if (resetModal.style.display != "none") {
    resetModal.style.display = "none";
    var userId = jQuery("#reset-user-id").val(); // 获取用户 ID
    jsonRequestWithToken(
      "/user/reset/photo/" + userId,
      "POST",
      {},
      function (data) {
        jQuery("#avatar" + userId).attr("src", getBaseUrl("/" + data));
      }
    );
    console.log("头像已重置");
  }
});

cancelReset.addEventListener("click", function () {
  resetModal.style.display = "none";
});

confirmUpdate.addEventListener("click", function () {
  if (updateModal.style.display != "none") {
    updateModal.style.display = "none";
    var selectedType = newUserType.value;
    if (selectedType) {
      var userId = jQuery("#update-user-id").val(); // 获取用户 ID
      jsonRequestWithToken(
        "/user/update/type/" + userId,
        "POST",
        {
          userType: parseInt(selectedType),
        },
        function (data) {
          jQuery("#userType" + userId).text(getType(selectedType));
        }
      );
      console.log("用户类型已更新为: " + selectedType);
    }
  }
});
cancelUpdate.addEventListener("click", function () {
  updateModal.style.display = "none";
});

queryUsers();

function queryUsers() {
  jsonRequestWithToken(
    "/user/query",
    "POST",
    {
      current: parseInt(jQuery("#input-current").val()),
      pageSize: parseInt(jQuery("#input-pageSize").val()),
      username: jQuery("#input-username").val(),
      nickname: jQuery("#input-nickname").val(),
      userType: parseInt(jQuery("#select-userType").val()),
    },
    function (data) {
      jQuery("#user-list").empty();

      jQuery("#input-current").val(data.current);
      jQuery("#input-pageSize").val(data.pageSize);
      jQuery("#res-current").text(data.current);
      jQuery("#res-pageSize").text(data.pageSize);
      jQuery("#res-total").text(data.total);
      jQuery("#res-pages").text(data.pages);

      var resultList = data.list;
      for (i = 0; i < resultList.length; i++) {
        var item = resultList[i];
        var user =
          '<div class="user-card"><img src="' + getBaseUrl("/" + item.photo);
        user += '" alt="头像" class="avatar" id="avatar' + item.id;
        user += '" /><div class="user-info"><span>用户名: ' + item.username;
        user += "</span><span>昵称: " + item.nickname;
        user += '</span><span>用户类型: <span id="userType' + item.id;
        user += '" >' + getType(item.userType);
        user +=
          '</span></span></div><div class="actions"><button class="reset-avatar" data-user-id="' +
          item.id;
        user +=
          '">重置头像</button><button class="update-type" data-user-id="' +
          item.id;
        user += '">更新用户类型</button></div></div>';
        jQuery("#user-list").append(jQuery(user));
      }
      initUserEvent();
    }
  );
}

function first() {
  jQuery("#input-current").val(1);
  queryUsers();
}
function last() {
  jQuery("#input-current").val(jQuery("#res-pages").text());
  queryUsers();
}
function prev() {
  jQuery("#input-current").val(parseInt(jQuery("#input-current").val()) - 1);
  queryUsers();
}
function next() {
  jQuery("#input-current").val(parseInt(jQuery("#input-current").val()) + 1);
  queryUsers();
}

function initUserEvent() {
  // 头像点击放大
  document.querySelectorAll(".avatar").forEach((avatar) => {
    avatar.addEventListener("click", function () {
      avatarModal.style.display = "flex";
      modalImg.src = this.src;
    });
  });

  // 重置头像模态框
  document.querySelectorAll(".reset-avatar").forEach((button) => {
    button.addEventListener("click", function () {
      jQuery("#reset-user-id").val(button.getAttribute("data-user-id"));
      resetModal.style.display = "flex";
    });
  });

  // 更新用户类型模态框
  document.querySelectorAll(".update-type").forEach((button) => {
    button.addEventListener("click", function () {
      var userId = button.getAttribute("data-user-id");
      jQuery("#update-user-id").val(userId);
      jQuery("#now-user-type").text(jQuery("#userType" + userId).text());
      jQuery("#new-user-type").val("");
      updateModal.style.display = "flex";
    });
  });
}

function getType(userType) {
  switch (userType) {
    case 0:
    case "0":
      return "封禁用户";
    case 1:
    case "1":
      return "普通用户";
    case 2:
    case "2":
      return "管理员";
    default:
      return "";
  }
}
