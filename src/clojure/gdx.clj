(ns clojure.gdx
  (:require [com.badlogic.gdx.gdx :as gdx]))

(defn app []
  (gdx/app))

; TODO each call that depends on 'Gdx' somewhere in the call chain
; (viewport update, sprite-batch, font-generator, camera?, texture?, batch?)
; put in here so dependencies are explicit?
; but the're allready in the 'folder' right?
