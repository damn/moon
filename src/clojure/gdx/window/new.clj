(ns clojure.gdx.window.new
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin Window)))

(defn f [^String title ^Skin skin]
  (Window. title skin))
