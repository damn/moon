(ns clojure.utils-click-listener
  (:require [clojure.click-listener :as click-listener]))

(defn create [f]
  (click-listener/new f))
