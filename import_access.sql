USE Analyst
GO

-- Create temp table and import xcel data to the table
SELECT * INTO dbo.ImportTempTable
FROM OPENROWSET('Microsoft.ACE.OLEDB.12.0',
                'Excel 12.0;IMEX=1;HDR=YES;Database=C:\Temp\access.xlsx',
                'SELECT * FROM [fb_export$]')
GO

-- Move data to Access table
INSERT INTO dbo.Access_Test
SELECT * FROM dbo.ImportTempTable
GO

-- Clean up data

-- Remove temp table
DROP TABLE dbo.ImportTempTable
GO
