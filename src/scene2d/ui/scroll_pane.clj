(ns scene2d.ui.scroll-pane
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane Skin)))

(defn create
  [{:keys [actor skin]}]
  (ScrollPane. ^Actor actor ^Skin skin))
