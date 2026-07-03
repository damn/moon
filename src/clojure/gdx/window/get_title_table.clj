(ns clojure.gdx.window.get-title-table
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f [^Window window]
  (Window/.getTitleTable window))
