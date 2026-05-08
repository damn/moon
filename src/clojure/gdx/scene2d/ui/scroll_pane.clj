(ns clojure.gdx.scene2d.ui.scroll-pane
  (:require [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [moon.ui.actor :as actor]))

(defmethod actor/create :ui/scroll-pane
  [opts]
  (scroll-pane/create opts))
