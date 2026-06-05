(ns clojure.scene2d.ui.scroll-pane
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane
                                               Skin)))

(defn create
  [{:keys [^Actor actor ^Skin skin]}]
  (ScrollPane. actor skin))
