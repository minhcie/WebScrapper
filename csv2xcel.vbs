'======================================
' Convert CSV to XLS
'
' arg1: source - CSV path\file
' arg2: target - Excel path\file
'======================================

srccsvfile = Wscript.Arguments(0)  
tgtxlsfile = Wscript.Arguments(1)  

'Create Spreadsheet
'Look for an existing Excel instance.
On Error Resume Next ' Turn on the error handling flag
Set objExcel = GetObject(,"Excel.Application")
'If not found, create a new instance.
If Err.Number = 429 Then  '> 0
  Set objExcel = CreateObject("Excel.Application")
End If

objExcel.Visible = false
objExcel.displayalerts = false

'Import CSV into Spreadsheet
Set objWorkbook = objExcel.Workbooks.open(srccsvfile)
Set objWorksheet1 = objWorkbook.Worksheets(1)

'Adjust width of columns
Set objRange = objWorksheet1.UsedRange
objRange.EntireColumn.Autofit()
'This code could be used to AutoFit a select number of  columns
'For intColumns = 1 To 17
'    objExcel.Columns(intColumns).AutoFit()
'Next

'Delete header row
'objExcel.Rows(1).Delete

'Make Headings Bold
'''objExcel.Rows(1).Font.Bold = TRUE

'Freeze header row
'''With objExcel.ActiveWindow
'''     .SplitColumn = 0
'''     .SplitRow = 1
'''End With
'''objExcel.ActiveWindow.FreezePanes = True

'Add Data Filters to Heading Row
'''objExcel.Rows(1).AutoFilter

'set header row gray
'''objExcel.Rows(1).Interior.ColorIndex = 15
'-0.249977111117893

'Turn off text wrapping
objWorksheet1.Rows.WrapText = false

'Save Spreadsheet, 51 = Excel 2007-2010, 56 = Excel 97-2003
objWorksheet1.SaveAs tgtxlsfile, 51

'Release Lock on Spreadsheet
objExcel.Quit()
Set objWorksheet1 = Nothing
Set objWorkbook = Nothing
Set ObjExcel = Nothing