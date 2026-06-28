(ns scene2d.ui.window.get-title-table
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f [^Window window]
  (.getTitleTable window))
