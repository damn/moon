(ns moon.reaction-txs.show-modal
  (:require [moon.ui :as ui])
  (:import (moon Stage)))

(defn do!
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   opts]
  (ui/show-modal-window! stage skin (Stage/.getViewport stage) opts)
  ctx)
