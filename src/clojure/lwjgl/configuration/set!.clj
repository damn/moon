(ns clojure.lwjgl.configuration.set!
  (:import (org.lwjgl.system Configuration)))

(defn f [configuration value]
  (Configuration/.set configuration value))
