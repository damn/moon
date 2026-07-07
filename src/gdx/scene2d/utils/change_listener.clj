(ns gdx.scene2d.utils.change-listener
  (:require [clojure.change-listener :as change-listener]))

(defn create [f]
  (change-listener/new f))
