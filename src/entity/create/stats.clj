(ns entity.create.stats
  (:require [moon.stats :as stats]))

(defn f
  [v _ctx]
  (stats/create v))
