(ns clojure.ui.cell.expand-y!
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn f [& args]
  (apply cell/expandY args))
