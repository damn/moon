(ns clojure.ui.cell.width!
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn f [& args]
  (apply cell/width args))
