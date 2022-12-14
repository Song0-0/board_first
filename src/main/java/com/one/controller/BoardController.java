package com.one.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.one.domain.BoardVO;
import com.one.domain.Criteria;
import com.one.domain.PageDTO;
import com.one.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@RequiredArgsConstructor
@Log4j
@RequestMapping("/board/*") // /board로 시작하는 모든 처리를 BoardController가 하도록 지정
public class BoardController {
	private final BoardService service;

//	@GetMapping("/list")
//	public void list(Model model) {// select한 전체를 넘겨줘야한다.
//		log.info("list............");
//		model.addAttribute("list", service.getList());
//	}

	@GetMapping("/list")
	public void list(Criteria cri, Model model) {
		log.info("list..............." + cri);
		model.addAttribute("list", service.getList(cri));
		int total = service.getTotalCount(cri);
		log.info("total : "+ total);
		model.addAttribute("pageMaker", new PageDTO(total, cri)); //123대신 이제 total을 넣어준다!!  123개 총 게시글을 가져오려면 dB에서 세서 가져와야겠지. 
	}

	@GetMapping("/register")
	public void register() {
		// 리턴이 없으면 메서드명과 같은 걸 불러온다.
	}

	@PostMapping("/register")
	public String register(BoardVO board, RedirectAttributes rttr) {
		log.info("register........." + board);

		Long bno = service.register(board);
		log.info("BNO======>" + bno);

		rttr.addFlashAttribute("result", board.getBno()); // 일회용으로 잠깐 사용하는 것이라서 flash로 주었다..
		return "redirect:/board/list";

	}

	/*
	 * @GetMapping("/get") public void get(@RequestParam("bno") Long bno, Model
	 * model) { log.info("get..........."); model.addAttribute("board",
	 * service.get(bno)); }
	 */

	@GetMapping({ "/get", "/modify" })
	public void get(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, Model model) {
		log.info("/get or modify..........");
		model.addAttribute("board", service.get(bno));
	}

	@PostMapping("/modify")
	public String modify(BoardVO board, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
		log.info("modify..........."+board);
		int count = service.modify(board);
		if (count == 1) {
			rttr.addFlashAttribute("result", "수정 성공");
		}

//		rttr.addAttribute("pageNum", cri.getPageNum());
//		rttr.addAttribute("amount", cri.getAmount());
//		rttr.addAttribute("type", cri.getType());
//		rttr.addAttribute("keyword", cri.getKeyword());
		return "redirect:/board/list" + cri.getListLink();
	}

	@PostMapping("/remove")
	public String remove(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
		log.info("remove..........." + bno);
		if (service.remove(bno)) {
			rttr.addFlashAttribute("result", "success");
		}

//		rttr.addAttribute("pageNum", cri.getPageNum());
//		rttr.addAttribute("amount", cri.getAmount());
//		rttr.addAttribute("type", cri.getType());
//		rttr.addAttribute("keyword", cri.getKeyword());
		return "redirect:/board/list" +cri.getListLink();
	}
}