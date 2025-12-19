(ns moon.tx.show-modal
  (:require [gdl.ui.stage :as stage]
            [moon.ui :as ui]))

(defn do!
  [{:keys [ctx/stage] :as ctx} opts]
  (ui/show-modal-window! stage (stage/viewport stage) opts)
  ctx)
