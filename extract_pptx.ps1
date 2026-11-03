$pptx = 'C:\Users\MAZEEF\Downloads\Android_Developer_Bootcamp_Week_1_Nov_3-6_2026.pptx'
$tmp = Join-Path $env:TEMP 'bootcamp_pptx_extract'
if (Test-Path $tmp) { Remove-Item $tmp -Recurse -Force }
New-Item -ItemType Directory -Path $tmp | Out-Null
Copy-Item $pptx (Join-Path $tmp 'deck.zip')
Expand-Archive (Join-Path $tmp 'deck.zip') -DestinationPath (Join-Path $tmp 'unzipped') -Force
$slidesDir = Join-Path $tmp 'unzipped\ppt\slides'
Get-ChildItem $slidesDir -Filter 'slide*.xml' |
  Sort-Object { [int]($_.BaseName -replace '\D','') } |
  ForEach-Object {
    Write-Output ("===== " + $_.Name + " =====")
    [xml]$x = Get-Content $_.FullName
    $texts = $x.SelectNodes('//*[local-name()="t"]') | ForEach-Object { $_.'#text' }
    Write-Output ($texts -join ' ')
    Write-Output ''
  }
