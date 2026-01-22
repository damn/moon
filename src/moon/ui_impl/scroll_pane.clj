(ns moon.ui-impl.scroll-pane
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane
                                               Skin)))

(defn create
  [{:keys [^Actor scroll-pane/actor
           ^Skin scroll-pane/skin]}]
  (ScrollPane. actor skin))
