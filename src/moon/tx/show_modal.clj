(ns moon.tx.show-modal
  (:require [moon.ui :as ui]
            [gdl.ui.stage :as stage]))

(defn do!
  [{:keys [ctx/stage] :as ctx} opts]
  (ui/show-modal-window! stage (stage/viewport stage) opts)
  ctx)
