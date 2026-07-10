(ns clojure.set-fill-parent!
  (:require [gdl.scenes.scene2d.utils.layout :as layout]))

(defn f [& args]
  (apply layout/set-fill-parent! args))
