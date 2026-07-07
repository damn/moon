(ns clojure.render-validate
  (:require [clojure.app-schema :refer [schema]]
            [clojure.validate-humanize :refer [validate-humanize]]))

(defn step [ctx]
  (validate-humanize schema ctx)
  ctx)
