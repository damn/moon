(ns scene2d.utils.change-listener
  (:require [clojure.gdx :as gdx]))

(defn create [f]
  (gdx/change-listener f))
