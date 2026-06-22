(ns moon.records.entity
  (:require [qrecord.core :as q]))

(q/defrecord R [
                entity/body
                ])
