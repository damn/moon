(ns clojure.gdx.scene2d.ui.scroll-pane
  (:require [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane
                                               Skin)))

(defmethod actor/create :ui/scroll-pane
  [{:keys [^Actor actor ^Skin skin]}]
  (ScrollPane. actor skin))
