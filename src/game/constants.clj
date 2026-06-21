(ns game.constants)

; if I set as ctx/key
; then at beginning render stage/get-ctx :stage/ctx
; sets it to old one
; even if I use postRunnable
; so we need a solution
(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(def spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def spiderweb-duration 5)

(def skill-image-radius-world-units
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(def mouseover-ellipse-width 5)

(def reaction-time-multiplier 0.016)
