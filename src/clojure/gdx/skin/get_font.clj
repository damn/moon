(ns clojure.gdx.skin.get-font
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn f [skin font-name]
  (.getFont skin font-name))
