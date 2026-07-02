(ns clojure.gdx.layout.set-fill-parent
  (:import (com.badlogic.gdx.scenes.scene2d.utils Layout)))

(defn f [^Layout layout fill-parent?]
  (Layout/.setFillParent layout fill-parent?))
