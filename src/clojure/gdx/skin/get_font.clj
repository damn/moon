(ns clojure.gdx.skin.get-font
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn f [skin font-name]
  (Skin/.getFont skin font-name))
