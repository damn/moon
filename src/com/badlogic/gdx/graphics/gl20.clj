(ns com.badlogic.gdx.graphics.gl20
  (:import (com.badlogic.gdx.graphics GL20)))

(def color-buffer-bit GL20/GL_COLOR_BUFFER_BIT)

; TODO this is not a mechanical translation here
; it should be 'gl-clear'
; and then now I am thinkning it should be 'glClear'
; with upper/lowerCase

; and maybe even one form per file

(defn clear! [gl bit-mask]
  (GL20/.glClear gl bit-mask))

(defn clear-color! [gl r g b a]
  (GL20/.glClearColor gl r g b a))
