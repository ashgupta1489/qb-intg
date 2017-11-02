/*<![CDATA[*/
function launchPopup(path) {
	var win;
	var checkConnect;
	var parameters = "location=1,width=800,height=650";
	parameters += ",left=" + (screen.width - 800) / 2 + ",top=" + (screen.height - 650) / 2;
	// Launch Popup
	win = window.open(path, 'connectPopup', parameters);
}
if(window.opener) {
	window.opener.location.href = '/'
	window.close()
}


$("#setupLnk").click(function() {
	$(".navbar-nav li").removeClass("active");
	$("#setupLnk").parent().addClass('active');
	$(".data-section").hide();
	$(".connection").show();
	//viewVendorsLink

});

$("#billPayments").click(function() {
	$(".navbar-nav li").removeClass("active");
	$("#billPayments").parent().addClass('active');
	$(".data-section").hide();
	$("#paymentData").html("");
	$.get("/getBillPayments", function(data) {
		data = $.parseJSON(data);
		if(data.response!=null && data.response.length!=0){
			$("#payments").hide();
			var hTxt = '<div class="alert alert-warning" role="alert">No data found. See below message<br/>'+data.response+'</div>';
			$("#no-payments").html(hTxt).show();
		}
		else{
			$.each(data, function(i, item) {
				var $tr = $('<tr>').append(
						$('<td scope="row">').text(i+1),$('</td>'),  $('<td>').text(item.vendorName),$('</td>'), 
						$('<td>').text(item.txnDate),$('</td>'), $('<td>').text(item.paymentType),$('</td>'),
						$('<td>').text(item.amountPaid),$('</td>'),  $('<td>').text(item.billTxnId),$('</td>'), $('</tr>'))
						.appendTo('#paymentTable');
			});
			$('#paymentTable').DataTable();
			$("#no-payments").hide()
			$("#payments").show();
		}
	})

});



$("#billLink").click(function() {
	$(".navbar-nav li").removeClass("active");
	$("#billLink").parent().addClass('active');
	$(".data-section").hide();
	$("#billData").html("");
	$.get("/getBills", function(data) {
		data = $.parseJSON(data);
		if(data.response!=null && data.response.length!=0){
			$("#bill").hide();
			var hTxt = '<div class="alert alert-warning" role="alert">No data found. See below message<br/>'+data.response+'</div>';
			$("#no-bills").html(hTxt).show();
		}
		else{
			$.each(data, function(i, item) {
				var paymentUrl = '<a class="btn btn-primary btn-sm payLinks"  data-toggle="modal" data-target="#paymentModal" href="#null" data-payId="'+item.txnNo+'">Make Payment</a>';
				//var paymentUrl = '<a class="btn btn-primary btn-sm payLinks"  href="#null" data-item="'+item.txnNo+'">Make Payment</a>';
				var $tr = $('<tr>').append(
						$('<td scope="row">').text(i+1),$('</td>'),  $('<td>').text(item.txnDate),$('</td>'),  $('<td>').text(item.payee),$('</td>'),
						$('<td>').text(item.category),$('</td>'),  $('<td>').text(item.dueDate),$('</td>'),  $('<td>').text(item.balance),$('</td>'),
						$('<td>').text(item.total),$('</td>'),$('<td>').html(paymentUrl)
						,$('</td>'), $('</tr>')).appendTo('#billTable');
			});
			$('#billTable').DataTable();
			$("#no-bills").hide();
			$("#bill").show();
		}
	})

});



$('#paymentModal').on('show.bs.modal', function (event) {
	  var button = $(event.relatedTarget) // Button that triggered the modal
	  var billId = button.data('payid') // Extract info from data-* attributes
	  var tr = button.closest("tr");
	  // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
	  // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
	  var modal = $(this);
	  modal.find('.modal-title').text('Make Payment for Bill # ' + billId);

	  $('#pymnt-date').datepicker({
		    startDate: "-0d",endDate: "+1m",autoclose: true,
		    maxViewMode: 0,orientation: "bottom right",todayHighlight: true
		});
	  $('#pymnt-date').datepicker('update', new Date());

	  modal.find('#pay-to').val(tr.find("td:eq(2)").text());
	  modal.find('#billId').val(billId);
	  modal.find('#amt').val(tr.find("td:eq(5)").text());
	  modal.find('#notes').val("For "+ tr.find("td:eq(3)").text() + " with bill # "+billId);
	  
	})
	
	function getFormData($form){
	    var unindexed_array = $form.serializeArray();
	    var indexed_array = {};
	
	    $.map(unindexed_array, function(n, i){
	        indexed_array[n['name']] = n['value'];
	    });
	    return indexed_array;
	}

	$("#doPayment").click(function(event) {
		event.preventDefault();
		$.ajax({
			  url: "/makePayment",
		    type: "POST",
		    data: JSON.stringify(getFormData($('#paymentForm'))),
		    contentType: "application/json",
		    complete: function(data) {
		    	console.log( "complete" + data);
		    	//TODO close popup and show bill paid 
		    	$('#paymentModal').modal('hide');
			  },

			  success: function(data) {
				  console.log("success"+ data);
					$('#paymentModal').modal('hide');
			 },

			  error: function(data) {
				  console.log( "error" + data);
					$('#paymentModal').modal('hide');
			  }
		});
	})

/*]]>*/