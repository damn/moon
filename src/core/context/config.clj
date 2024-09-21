(ns core.context.config
  (:require [core.component :refer [defcomponent] :as component]
            [core.property :as property]))

(property/def-attributes
  :tag [:enum [:dev :prod]]
  :configs :some)

(defcomponent :context/config
  {:data [:map [:tag :configs]]
   :let {:keys [tag configs]}}
  (component/create [_ _ctx]
    (get configs tag)))