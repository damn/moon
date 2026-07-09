(ns com.badlogic.gdx.scenes.scene2d.ui.scroll-pane
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane Skin)
           ))

(defn new [^Actor actor ^Skin skin]
  (ScrollPane. actor skin))
