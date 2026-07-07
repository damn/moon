(ns clojure.set!
  (:refer-clojure :exclude [set!])
  (:import (org.lwjgl.system Configuration)))

(defn f [configuration value]
  (Configuration/.set configuration value))
