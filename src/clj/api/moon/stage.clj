(ns clj.api.moon.stage
  (:import (moon Stage)))

(defn create [viewport batch]
  (Stage. viewport batch))
