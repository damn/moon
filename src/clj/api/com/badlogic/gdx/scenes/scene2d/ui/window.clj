(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(defn create [^String title ^Skin skin]
  (Window. ^String title ^Skin skin))

(defn title-table [^Window window]
  (.getTitleTable window))
