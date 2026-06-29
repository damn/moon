(ns game.constants)

; if I set as ctx/key
; then at beginning render stage/get-ctx :stage/ctx
; sets it to old one
; even if I use postRunnable
; so we need a solution
; _. just postRunnable set! .ctx stage ...
(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)
(def ^:dbg-flag show-body-bounds? false)
