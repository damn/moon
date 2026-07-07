(ns clojure.k-destroy
  (:require [clojure.destroy-audiovisual :as destroy-audiovisual]))

(def k->destroy
  {:entity/destroy-audiovisual destroy-audiovisual/f})
