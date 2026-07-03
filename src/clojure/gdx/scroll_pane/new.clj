(ns clojure.gdx.scroll-pane.new
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane Skin)))

(defn f [^Actor actor ^Skin skin]
  (ScrollPane. actor skin))
