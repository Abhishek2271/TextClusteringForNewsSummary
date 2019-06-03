$(document).ready(function () {
    $('#load_data').click(function () {
        $.ajax({
            url: "TopStories.csv",
            dataType: "text",
            success: function (data) {
                var employee_data = data.split(/\r?\n|\r/);
                var table_data;

                for (var count = 0; count < employee_data.length; count++) {

                    var cell_data = employee_data[count].split(",");

                    var str = "true";
                    var _status = String(cell_data[2]);
                    var _bool = _status.includes(str);
                    console.log(_bool);
                    if (_bool) {

                        var cell_data = employee_data[count].split(",")[0];



                        var _date = String(cell_data[1]);

                        if (_date.length < 3) {
                            var today = new Date();
                            var dd = today.getDate();
                            var mm = today.getMonth() + 1; //January is 0!
                            var yyyy = today.getFullYear();

                            if (dd < 10) {
                                dd = '0' + dd;
                            }

                            if (mm < 10) {
                                mm = '0' + mm;
                            }

                            today = mm + '/' + dd + '/' + yyyy;
                            _date =today;
                        }



                        table_data += '<div  class="pi-news-date">'
                            +
                            ' <span>' + '</span>'
                            +
                            '</div>' +
                            ' <h2 class="h5 pi-margin-top-minus-5 pi-margin-bottom-5">' +
                            '<a href="#" class="pi-link-dark">';






                        for (var cell_count = 0; cell_count < cell_data.length; cell_count++) {


                            table_data += cell_data[cell_count];


                        }


                        table_data += '</a>' +
                            '</h2>' +
                            '<ul class="pi-meta pi-margin-bottom-10">' +

                            '    <li><i class="icon-clock"></i>' + _date + '</li>' +

                            ' </ul>' +


                            ' <hr class="pi-divider pi-divider-dashed">';
                    }

                }

                $('#employee_table').html(table_data);
            }
        });
    });

});