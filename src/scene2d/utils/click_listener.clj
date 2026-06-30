(ns scene2d.utils.click-listener
  (:require [clojure.gdx :as gdx]))

(defn create [f]
  (gdx/click-listener f))
