(ns moon.ui.window
  (:require [cdq.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

; FIXME opts not there anymore
; TODO cannot close !
; TODO WASD in textfield -> player moves -> InputMultiplexer?
(defn create
  [{:keys [title
           close-button?
           center?
           close-on-escape?]}]
  (let [#_show-window-border? #_true
        window (Window. title ui/skin)]
    #_(when close-button?    (.addCloseButton window))
    #_(when center?          (.centerWindow   window))
    #_(when close-on-escape? (.closeOnEscape  window))
    window))
