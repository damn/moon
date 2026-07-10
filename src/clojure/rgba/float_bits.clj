(ns clojure.rgba.float-bits
  (:require [gdl.graphics.color :as color]))

(defn f [& args]
  (apply color/to-float-bits args))
