(ns clojure.ui.window
  (:refer-clojure :exclude [class])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(def class Window)

(defn create [title skin]
  (Window. ^String title ^Skin skin))
