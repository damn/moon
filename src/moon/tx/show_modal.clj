(ns moon.tx.show-modal
  (:require [moon.ui.stage :as stage]
            [moon.ui :as ui]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   opts]
  (ui/show-modal-window! stage skin (stage/viewport stage) opts)
  ctx)
