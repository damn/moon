(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn title-table [^Window window]
  (.getTitleTable window))
