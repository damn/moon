(ns gdx.math.rectangle.contains
  (:require [clojure.gdx :as gdx]))

(defn contains? [rectangle [x y]]
  (gdx/rectangle-contains? rectangle x y))
