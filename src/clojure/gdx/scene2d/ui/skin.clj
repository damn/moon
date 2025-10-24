(ns clojure.gdx.scene2d.ui.skin
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn font [^Skin skin name]
  (.getFont skin name))
