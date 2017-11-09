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
});

$("#billPayments").click(function() {
	$(".navbar-nav li").removeClass("active");
	$("#billPayments").parent().addClass('active');
	$(".data-section").hide();
	$("#paymentData").html("");
	$.get("/getBillPayments", function(data) {
		data = $.parseJSON(data);
		if(data.response!=null && data.response.length!=0){
			var hTxt = '<div class="alert alert-warning" role="alert">No bill payments found. See below message<br/><br/><span class="font-weight-bold font-italic pl-5">"'+data.response+'"</span><br/></div>';
			$("#no-data").html(hTxt).show();
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
			$("#no-data").empty();
			$("#payments").show();
		}
	})

});


$("#viewVendors").click(function() {
	$(".navbar-nav li").removeClass("active");
	$(this).parent().addClass('active');
	$(".data-section").hide();
	$("#vendorData").html("");
	$.get("/getVendors", function(data) {
		data = $.parseJSON(data);
		if(data.response!=null && data.response.length!=0){
			var hTxt = '<div class="alert alert-warning" role="alert">No vendor found. See below message<br/><br/><span class="font-weight-bold font-italic pl-5">"'+data.response+'"</span><br/></div>';
			$("#no-data").html(hTxt).show();
			$("#add-vendors").show();
		}
		else{
			$.each(data, function(i, item) {
				var vendorEditUrl = '<a class="btn btn-primary btn-sm vendorLinks"  data-toggle="modal" data-target="#vendorModal" href="#null" data-vendorid="'+item.vendorId+'">Edit Vendor</a>';
				var $tr = $('<tr>').append(
								$('<td scope="row">').text(i+1),$('</td>'),
								$('<td>').text(item.displayName),$('</td>'), 
								$('<td>').text(item.primaryEmail),$('</td>'),
								$('<td>').text(item.phnNum),$('</td>'),
								$('<td>').text(item.balance),$('</td>'),
								$('<td>').text(item.accountNumber),$('</td>'),
								$('<td>').text(item.routingNumber),$('</td>'),
								$('<td>').html(vendorEditUrl),$('</td>'),
								$('</tr>'))
						.appendTo('#vendorTable');
			});
			$('#vendorTable').DataTable();
			$("#no-data").empty();
			$("#add-vendors").show();
			$("#vendors").show();
		}
	})

});



$("#billLink").click(function(e) {
	e.preventDefault();
	$(".navbar-nav li").removeClass("active");
	$("#billLink").parent().addClass('active');
	$(".data-section").hide();
	$("#billData").html("");
	$.get("/getBills", function(data) {
		data = $.parseJSON(data);
		if(data.response!=null && data.response.length!=0){
			var hTxt = '<div class="alert alert-warning" role="alert">No bill found. See below message<br/><br/><span class="font-weight-bold font-italic pl-4 pr-2">"'+data.response+'"</span><br/></div>';
			$("#no-data").html(hTxt).show();
		}
		else{
			$.each(data, function(i, item) {
				var paymentUrl = '<a class="btn btn-primary btn-sm payLinks"  data-toggle="modal" data-target="#paymentModal" href="#null" data-payId="'+item.txnNo+'">Make Payment</a>';
				//var paymentUrl = '<a class="btn btn-primary btn-sm payLinks"  href="#null" data-item="'+item.txnNo+'">Make Payment</a>';
				var $tr = $('<tr>').append(
						$('<td scope="row">').text(i+1),$('</td>'),
						$('<td>').text(item.txnDate),$('</td>'),
						$('<td>').text(item.payee),$('</td>'),
						$('<td>').text(item.category),$('</td>'),
						$('<td>').text(item.dueDate),$('</td>'),
						$('<td>').text(item.balance),$('</td>'),
						$('<td>').text(item.total),$('</td>'),
						$('<td>').html(paymentUrl),$('</td>'), 
						$('</tr>')).appendTo('#billTable');
			});
			$('#billTable').DataTable();
			$("#no-data").empty();
			$("#bill").show();
		}
	})

});



$('#paymentModal').on('shown.bs.modal', function (event) {
	  var button = $(event.relatedTarget) // Button that triggered the modal
	  var billId = button.data('payid') // Extract info from data-* attributes
	  var tr = button.closest("tr");
	  var modal = $(this);
	  modal.find('.modal-title').text('Make Payment for Bill # ' + billId);

	 
	 

	  modal.find('#pay-to').val(tr.find("td:eq(2)").text());
	  modal.find('#billId').val(billId);
	  modal.find('#amt').val(tr.find("td:eq(5)").text());
	  modal.find('#notes').val("For "+ tr.find("td:eq(3)").text() + " with bill # "+billId);
	  
	})
	
	 $('#pymnt-date').datepicker({
		    startDate: "-0d",endDate: "+1m",autoclose: true,
		    maxViewMode: 0,orientation: "bottom right",todayHighlight: true
		});
	$('#pymnt-date').datepicker('update', new Date());
	
	
	
	$('#vendorModal').on('show.bs.modal', function (event) {
	  var button = $(event.relatedTarget) // Button that triggered the modal
	  var id = button.data('vendorid') // Extract info from data-* attributes
	  var tr = button.closest("tr");
	  var modal = $(this);
	  var $inputs =  modal.find('input');
	  $inputs.filter('.edit').val("");
	  if(id!=-1){
		  $inputs.not(".edit").prop( "disabled", true );
		  $inputs.not(".edit").prop( "readonly", true );
		  //add plain text class
		  modal.find('.modal-title').text('Edit Vendor # ' + id);
		  modal.find('#displayName').val(tr.find("td:eq(1)").text()).removeClass('form-control').addClass('form-control-plaintext');
		  modal.find('#companyName').val(tr.find("td:eq(1)").text()).removeClass('form-control').addClass('form-control-plaintext');
		  modal.find('#primaryEmail').val(tr.find("td:eq(2)").text());
		  modal.find('#phnNum').val(tr.find("td:eq(3)").text());
		  modal.find('#vendorId').val(id);
		  modal.find('#amt').val(tr.find("td:eq(4)").text());
		  modal.find('#accountNumber').val(tr.find("td:eq(5)").text());
		  modal.find('#routingNumber').val(tr.find("td:eq(6)").text());
	  }
	  else{
		  $('#vendorForm').trigger("reset");
		  $inputs.prop( "disabled", false );
		  $inputs.prop( "readonly", false );
		  $inputs.removeClass('form-control-plaintext').addClass('form-control');
	  }
	  
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
		    	//document.location.reload();
		    	$( "#billLink" ).trigger( "click" );
			  },

			  success: function(data) {
				  console.log("success"+ data);
			 },

			  error: function(data) {
				  console.log( "error" + data);
					$('#paymentModal').modal('hide');
					//document.location.reload();
					$( "#billLink" ).trigger( "click" );
			  }
		});
	})

	$("#saveVendor").click(function(event) {
		event.preventDefault();
		$.ajax({
			  url: "/saveVendor",
		    type: "POST",
		    data: JSON.stringify(getFormData($('#vendorForm'))),
		    contentType: "application/json",
		    complete: function(data) {
		    	console.log( "complete" + data);
		    	//TODO close popup and show bill paid 
		    	$('#vendorModal').modal('hide');
		    	//document.location.reload();
		    	$( "#viewVendors" ).trigger( "click" );
			  },

			  success: function(data) {
				  console.log("success"+ data);
			 },

			  error: function(data) {
				  console.log( "error" + data);
					$('#vendorModal').modal('hide');
					//document.location.reload();
					$( "#viewVendors" ).trigger( "click" );
			  }
		});
	})
	

/*]]>*/