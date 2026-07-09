(ns clojure.ui.cell.fill-y!
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]))

(defn f [& args]
  (apply cell/fill-y args))
