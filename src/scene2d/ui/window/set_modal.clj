(ns scene2d.ui.window.set-modal
  (:require [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

(defn f! [window modal?]
  (window/set-modal! window modal?))
